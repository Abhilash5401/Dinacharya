import { useEffect } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { WebSocketEvent } from '@/types';

const WS_URL = import.meta.env.VITE_WS_URL || 'http://localhost:8080/api/v1/ws';

export const useWebSocket = (teamId: string | undefined) => {
  const queryClient = useQueryClient();

  useEffect(() => {
    if (!teamId) return;

    const client = new Client({
      webSocketFactory: () => new SockJS(WS_URL) as any,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.onConnect = () => {
      console.log('WebSocket connected');

      client.subscribe(`/topic/teams/${teamId}`, (message) => {
        const event: WebSocketEvent = JSON.parse(message.body);

        switch (event.type) {
          case 'TASK_CREATED':
          case 'TASK_UPDATED':
            queryClient.invalidateQueries({ queryKey: ['tasks'] });
            if (event.task) {
              queryClient.setQueryData(['tasks', event.task.id], event.task);
            }
            break;

          case 'TASK_DELETED':
            queryClient.invalidateQueries({ queryKey: ['tasks'] });
            break;

          case 'COMMENT_ADDED':
          case 'COMMENT_DELETED':
            if (event.taskId) {
              queryClient.invalidateQueries({ queryKey: ['comments', event.taskId] });
            }
            break;

          case 'ATTACHMENT_ADDED':
          case 'ATTACHMENT_DELETED':
            if (event.taskId) {
              queryClient.invalidateQueries({ queryKey: ['attachments', event.taskId] });
            }
            break;
        }
      });
    };

    client.onStompError = (frame) => {
      console.error('WebSocket error:', frame);
    };

    client.activate();

    return () => {
      client.deactivate();
    };
  }, [teamId, queryClient]);
};
