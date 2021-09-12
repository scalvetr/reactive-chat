import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {Message} from './message.model';
import {HttpClient} from '@angular/common/http';
import {IdentitySerializer, JsonSerializer, RSocketClient} from 'rsocket-core';
import {Flowable} from 'rsocket-flowable';
import RSocketWebSocketClient from 'rsocket-websocket-client';
import {ISubscription, Payload, ReactiveSocket} from 'rsocket-types';
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
  readonly NUMBER_OF_REQUESTED_ITEMS = 100;

  private counter = this.NUMBER_OF_REQUESTED_ITEMS;
  private paused = false;

  constructor(private http: HttpClient) {
    this.initSocket();
  }

  public messagesObservable(): Observable<Message> {
    return this.receiverSubject.asObservable();
  }

  private encodeRoute(route: string): string {
    return String.fromCharCode(route.length) + route;
  }

  public initSocket(): void {
    const channelRoutingMetadata = this.encodeRoute(environment.messagesService.channelEndpoint);
    const sendRoutingMetadata = this.encodeRoute(environment.messagesService.sendMessagesEndpoint);
    const receiveRoutingMetadata = this.encodeRoute(environment.messagesService.receiveMessagesEndpoint);

    let rsocketUrl = environment.messagesService.rsocketUrl;
    if (rsocketUrl.startsWith('/')) {
      const l = window.location;
      rsocketUrl = ((l.protocol === 'https:') ? 'wss://' : 'ws://') + l.hostname +
        (((l.port !== '80') && (l.port !== '443')) ? ':' + l.port : '') +
        rsocketUrl;
    }
    const client = new RSocketClient({
      // send/receive objects instead of strings/buffers
      serializers: {
        data: JsonSerializer,
        metadata: IdentitySerializer
      },
      setup: {
        // ms btw sending keepalive to server
        keepAlive: 60000,
        // ms timeout if no keepalive response
        lifetime: 180000,
        // format of `data`
        dataMimeType: 'application/json',
        // format of `metadata`
        metadataMimeType: 'message/x.rsocket.routing.v0'
      },
      transport: new RSocketWebSocketClient({
        url: rsocketUrl
      })
    });
    const self = this;

    // The data and metadata parameters could be used by the handler() payload parameter on the back end side
    const sender = this.senderSubject.asObservable();
    const flowable: Flowable<Message> = new Flowable<Message>(source => {
      console.log('build flowable sender');
      source.onSubscribe({
        cancel: () => {
          console.log('source.onSubscribe.cancel()');
        },
        request: (n) => {
          console.log('source.onSubscribe.request(%s)', n);
        }
      });
      sender.subscribe({
        next: value => {
          console.log('sender.next(%s)', value);
          source.onNext(value);
        },
        error: error => {
          console.log('sender.error(%s)', error);
          source.onError(error);
        },
        complete: () => {
          console.log('sender.complete()');
          source.onComplete();
        }
      });
    });
    // Connect to the back end RSocket and request a stream (connects to the handler() method in NewsSocket.kt)

    client.connect().then((socket: ReactiveSocket<any, any>) => {
      /*flowable.map(message => socket.fireAndForget({
        metadata: sendRoutingMetadata,
        data: message
      }));
      socket.requestStream({
        metadata: receiveRoutingMetadata,
        data: undefined
      })*/
      socket.requestChannel(flowable.map(message => {
        return {
          metadata: channelRoutingMetadata,
          data: message
        };
      })).subscribe({
        onComplete: () => {
          console.log('requestChannel: onComplete()');
        },
        onError: (error: Error) => {
          console.log('requestChannel: onError(%s)', error.message);
        },
        onNext: payload => {
          console.log('requestChannel: onNext(%s)', payload);
          self.handlePayload(payload);
          self.requestMoreDataIfNeeded();
        },
        onSubscribe: (sub: ISubscription) => {
          console.log('requestChannel: onSubscribe(%s)', sub);
          self.subscription = sub;
          console.log('requestChannel: sub.request(%s)', self.NUMBER_OF_REQUESTED_ITEMS);
          sub.request(self.NUMBER_OF_REQUESTED_ITEMS);
          console.log('requestChannel: successfully requested');
        }
      });
    }, error => console.error(error));
  }


  private handlePayload(payload: Payload<Message, any>): void {
    console.log('handlePayload: ' + payload.data);
    this.receiverSubject.next(payload.data);
  }

  private requestMoreDataIfNeeded(): void {
    console.log('requestMoreDataIfNeeded: ' + !this.paused);
    if (!this.paused) {
      this.counter--;
      console.log('requestMoreDataIfNeeded: counter=' + this.counter);
      if (this.subscription != null && this.counter <= 0) {
        this.subscription.request(this.NUMBER_OF_REQUESTED_ITEMS);
        console.log('requestMoreDataIfNeeded: sub.request(%s)', this.NUMBER_OF_REQUESTED_ITEMS);
        this.counter = this.NUMBER_OF_REQUESTED_ITEMS;
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
