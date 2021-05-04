// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  messagesService: {
    rsocketUrl: 'ws://localhost:8080/rsocket',
    messagesEndpoint: 'api.v1.messages.stream'
  },
  authService: {
    avatarBaseUrl: 'http://localhost:8080/avatars/'
  }
};
