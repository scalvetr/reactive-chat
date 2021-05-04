import {User} from './user.model';

export class Message {
  constructor(public sender: User, public content: string, public timestamp: Date, public id: null | number = null) {
  }
}
