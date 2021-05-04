import {Component, OnInit} from '@angular/core';
import {Message} from '../shared/message.model';
import {MessageService} from '../shared/message.service';
import {NgForm} from '@angular/forms';
import {AuthService} from '../shared/auth.service';
import {User} from '../shared/user.model';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {
  error: string | null = null;
  textbox = '';
  currentUser: User | null = null;
  messages: Message[] = new Array();

  constructor(
    private messageService: MessageService,
    private authService: AuthService) {
  }

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.messages = [];
    this.messageService.messagesObservable().subscribe(
      success => {
        this.error = null;
        this.messages.push(success);
      },
      error => { // second parameter is to listen for error
        console.log(error);
        this.error = 'Error getting message list';
      }
    );
  }

  onSubmit(f: NgForm): void {
    console.log(f.valid);
    if (f.valid && this.currentUser != null) {
      const newMessage = new Message(this.currentUser, this.textbox, new Date());
      this.messageService.sendMessage(newMessage).subscribe(
        message => {
          console.log('successfully sent = ' + message);
          this.textbox = '';
        },
        error => { // second parameter is to listen for error
          console.log(error);
          this.error = 'Error sending message list';
        }
      );
    }
  }

}
