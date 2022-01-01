// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  messagesService: {
    wsUrl: 'ws://localhost:8080/ws',
    sendMessagesEndpoint: 'api.v1.messages.stream', // send to the receiving stream in the server side
    receiveMessagesEndpoint: 'api.v1.messages.stream' // receive from the sending stream in the server side
  },
  authService: {
    avatarBaseUrl: 'http://localhost:8080/avatars/'
  }
};
