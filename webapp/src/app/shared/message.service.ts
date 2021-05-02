import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {Message} from './message.model';
import {HttpClient} from '@angular/common/http';
import {JsonSerializers, RSocketClient} from 'rsocket-core';
import RSocketWebSocketClient from 'rsocket-websocket-client';
import {Payload, ReactiveSocket, ISubscription} from 'rsocket-types';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  private receiverSubject = new Subject<Message>();
  private senderSubject = new Subject<Message>();
  private subscription: ISubscription | null = null;

  // With RSocket we define how many items we want from the producer. Similarly as RX operators, this can even be unbounded
  // (set to MAX_STREAM_ID from roscket-core). In this case the websocket continually receives
  // data from the backend (if available). If the data flow is intense and the client has hiccups
  // it ca freeze the client and the websocketconnection dies on the server side (DirectMemoryError).
  // We'll set it to a mere 1, and continually request the next one. Performance wise this should be higher.
  readonly SINGLE_REQ = 1;

  private counter = this.SINGLE_REQ;
  private paused = false;

  constructor(private http: HttpClient) {
    this.initSocket();
  }

  public messagesObservable(): Observable<Message> {
    return this.receiverSubject.asObservable();
  }

  public initSocket(): void {
    const client = new RSocketClient({
      // send/receive objects instead of strings/buffers
      serializers: JsonSerializers,
      setup: {
        // ms btw sending keepalive to server
        keepAlive: 60000,
        // ms timeout if no keepalive response
        lifetime: 180000,
        // format of `data`
        dataMimeType: 'application/json',
        // format of `metadata`
        metadataMimeType: 'application/json',
      },
      transport: new RSocketWebSocketClient({
        url: environment.messagesService.rsocketEndpoint,
        wsCreator: (url: string) => {
          return new WebSocket(url);
        }
      })
    });
    const self = this;
    // Connect to the back end RSocket and request a stream (connects to the handler() method in NewsSocket.kt)
    client.connect().subscribe({
      onComplete: (socket: ReactiveSocket<Message, any>) => {
        // The data and metadata parameters could be used by the handler() payload parameter on the back end side
        socket.requestStream({data: undefined, metadata: ''}).subscribe({
          onComplete: () => {
            console.log('onComplete()');
          },
          onError: (error: Error) => {
            console.log('onError(%s)', error.message);
          },
          onNext: (payload: Payload<Message, any>) => {
            self.handlePayload(payload);
            self.requestMoreDataIfNeeded();
          },
          onSubscribe: (sub: ISubscription) => {
            self.subscription = sub;
            sub.request(self.SINGLE_REQ);
          }
        });
        self.senderSubject.asObservable().subscribe(
          newMessage => {
            socket.fireAndForget({data: newMessage, metadata: ''});
          },
          error => { // second parameter is to listen for error
            console.log(error);
          }
        );
      },
      onError: error => console.error(error),
      onSubscribe: cancel => {/* call cancel() to abort */
      }
    });
  }


  private handlePayload(payload: Payload<Message, any>): void {
    this.receiverSubject.next(payload.data);
  }

  private requestMoreDataIfNeeded(): void {
    if (!this.paused) {
      this.counter--;
      if (this.subscription != null && this.counter <= 0) {
        this.subscription.request(this.SINGLE_REQ);
        this.counter = this.SINGLE_REQ;
      }
    }
  }

  // send message to server
  sendMessage(newMessage: Message): Observable<Message> {
    console.log('sendMessage:' + JSON.stringify(newMessage));
    this.senderSubject.next(newMessage);
    return new Observable(subscriber => {
      subscriber.next(newMessage);
    });
  }


}
