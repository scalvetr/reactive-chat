export class Message {
  constructor(public sender: string, public content: string, public timestamp: Date, public id: null | number = null) {
  }
}
