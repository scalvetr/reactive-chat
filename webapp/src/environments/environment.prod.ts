export const environment = {
  production: true,
  messagesService: {
    wsUrl: '/ws',
    sendMessagesEndpoint: 'api.v1.messages.stream', // send to the receiving stream in the server side
    receiveMessagesEndpoint: 'api.v1.messages.stream' // receive from the sending stream in the server side
  },
  authService: {
    avatarBaseUrl: '/avatars/'
  }
};
