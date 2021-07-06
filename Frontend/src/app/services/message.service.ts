import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
// Declare SockJS and Stomp
declare var SockJS: any;
declare var Stomp: any;
@Injectable({
  providedIn: 'root'
})
export class MessageService {
  
  constructor(private http: HttpClient) {
    this.initializeWebSocketConnection();
    this.getMessages();
  }
  public stompClient: any;
  public todos: any[] = [];
  initializeWebSocketConnection() {
    const serverUrl = 'http://localhost:9000/socket';
    const ws = new SockJS(serverUrl);
    this.stompClient = Stomp.over(ws);
    const that = this;
    // tslint:disable-next-line:only-arrow-functions
    this.stompClient.connect({}, function(frame: any) {
      that.stompClient.subscribe('/message', (message: any) => {
        
        if (message.body) {
          let todo = JSON.parse(message.body);
          console.log(`received todo:`, todo);
          // TODO: Handle toggle completed
          const _todoIndex = that.todos.findIndex(e => e.id == todo.id);
          console.log(`_todo = `, _todoIndex);
          if(_todoIndex != -1){
            that.todos[_todoIndex].completed = todo.completed;
          }else{
            that.todos.push(todo);
          }
        }
      });
    });
  }
  
  sendMessage(title: string) {
    // let todo: Todo = {
    //   id: null,
    //   title: title,
    //   isCompleted: false
    // }
    // console.log(`todo =`, todo);
    // let str = JSON.stringify(todo);
    this.stompClient.send('/app/send/message' , {}, title);
  }

  getMessages(){
    console.log(`init getMessages`);
    
    this.http.get<any[]>('http://localhost:9000/get/message').subscribe(todoList => {
      console.log(todoList);
      this.todos = todoList;
    })
  }
}

interface Todo {
  id: number | null;
  title: string;
  isCompleted: boolean;
}
