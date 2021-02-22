import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Message} from './message.model';
import {environment} from '../../environments/environment';
import {Observable, Subject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  defaultHttpOptions = {};

  constructor(private http: HttpClient) {
  }

  sendMessage(newMessage: Message): Observable<any> {
    newMessage.timestamp = new Date();
    console.log('sendMessage = ' + newMessage);
    return this.http.post<any>(environment.messagesService.postMessage, newMessage,
      {
        ...this.defaultHttpOptions, ...{
          observe: 'response'
        }
      });
  }

  messagesSubscribe(): Observable<Message> {
    const eventSource = new EventSource(environment.messagesService.getMessagesStream);
    const events = new Subject<Message>();
    eventSource.addEventListener('error', error => events.error(error));
    eventSource.addEventListener('message', message => {
      const data = JSON.parse(message.data) as Message;
      events.next(data);
    });

    return events.asObservable();
  }
}
