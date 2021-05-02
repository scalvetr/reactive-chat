// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  messagesService: {
    postMessage: 'http://localhost:8080/messages',
    getMessagesStream: 'http://localhost:8080/messages',
    rsocketEndpoint: 'ws://localhost:8080/rsocket'
  }
};
