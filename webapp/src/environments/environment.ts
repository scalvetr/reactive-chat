// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  messagesService: {
    rsocketUrl: 'ws://localhost:8080/ws',
    channelEndpoint: 'api.v1.messages.channel',
    sendMessagesEndpoint: 'api.v1.messages.receive', // send to the receiving stream in the server side
    receiveMessagesEndpoint: 'api.v1.messages.send' // receive from the sending stream in the server side
  },
  authService: {
    avatarBaseUrl: 'http://localhost:8080/avatars/'
  }
};
