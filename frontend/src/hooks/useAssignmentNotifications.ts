import { useEffect, useRef } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { toast } from 'react-toastify';
import { Task, WebSocketEvent } from '@/types';

const WS_URL = import.meta.env.VITE_WS_URL || 'http://localhost:8080/api/v1/ws';

export function useAssignmentNotifications(userId?: string) {
  const queryClient = useQueryClient();
  const seen = useRef(new Set<string>());

  useEffect(() => {
    if (!userId) return;

    const client = new Client({
      webSocketFactory: () => new SockJS(WS_URL) as any,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.onConnect = () => {
      client.subscribe(`/topic/users/${userId}`, (message) => {
        const event: WebSocketEvent = JSON.parse(message.body);
        const task = event.task as Task | undefined;
        if (event.type !== 'TASK_ASSIGNED' || !task) return;
        if (seen.current.has(task.id)) return;
        seen.current.add(task.id);
        queryClient.invalidateQueries({ queryKey: ['tasks'] });
        toast.info(`New task assigned: ${task.title}`);
      });
    };

    client.activate();
    return () => {
      client.deactivate();
    };
  }, [userId, queryClient]);
}
