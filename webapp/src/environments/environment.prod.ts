export const environment = {
  production: true,
  messagesService: {
    rsocketUrl: '/ws',
    channelEndpoint: 'api.v1.messages.channel',
    sendMessagesEndpoint: 'api.v1.messages.receive', // send to the receiving stream in the server side
    receiveMessagesEndpoint: 'api.v1.messages.send' // receive from the sending stream in the server side
  },
  authService: {
    avatarBaseUrl: '/avatars/'
  }
};
