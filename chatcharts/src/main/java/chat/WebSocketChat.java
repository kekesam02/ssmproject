package chat;

import entity.User;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


@ServerEndpoint(value="/chat")
public class WebSocketChat {
	/**
	 * 定义Set集合，将所有WebSocket端点实例放入，用于群发。
	 */
	private static Set<WebSocketChat> connections = new HashSet<WebSocketChat>();
    // User类的对象引用
	private User user;
	/**
	 * 类似于Servlet中的Session，表示一次会话，这里将Session作为一个属性，在群发时候会用到
	 */
	private Session session;
	/**
	 * 当一个连接打开的时候该方法会执行，同时传入一个Session（表示一次会话）
	 * @param session
	 */
	@OnOpen
	public void start(Session session) {
		//获取用户的id，并将id打印出来
		this.session = session;
		int id = Integer.parseInt(session.getId());
		System.out.println("hello "+id);
		user = new User();
		user.setId(id);
		/**
		 * 将该WebSocket端点实例放入Set集合
		 */
		connections.add(this);
	}
	/**
	 * 用来接收消息
	 * @param msg
	 */
	@OnMessage
	public void incoming(String msg) {
		broadcast(msg);
	}


	@OnClose
	public void end() {
		connections.remove(this);
	}



	/**
	 * 用来广播消息（群发），传入要发送的消息，将此消息发送到Set集合中的所有的人.
	 * @param msg
	 */
	private static void broadcast(String msg) {
		// foreach遍历Set集合
		for (WebSocketChat client : connections) {
			try {
				//将信息发送给用户
				client.session.getBasicRemote().sendText(msg);
			} catch (IOException e) {
				e.printStackTrace();
				//删除集合中这个端点
				connections.remove(client);
				try {
					client.session.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}