var REQUEST_TYPE = {
    ROOM_LIST: "ROOM_LIST",
    CREATE_ROOM: "CREATE_ROOM",
    JOIN_ROOM: "JOIN_ROOM",
    QUIT_ROOM: "QUIT_ROOM",
    START_GAME: "START_GAME",
    DISPATCH: "DISPATCH",
    FIRE: "FIRE",
}
var RESPONSE_TYPE = {
    CONNECT: "CONNECT",
    ROOM_LIST: "ROOM_LIST",
    CREATE_ROOM: "CREATE_ROOM",
    JOIN_ROOM: "JOIN_ROOM",
    START_GAME: "START_GAME",
    DISPATCH: "DISPATCH",
    FIRE: "FIRE",
}
var socket;
var host = "http://localhost:8080";
var app = new Vue({
    el: '#main-content',
    data: function() {
        return {
            userId: "",
            connected: false,
            started: false,
            userRequest: {
                roomId: -1,
                type: "",
                mahjong: "",
            },

            roomList: [],

            roomSelected: '',

            room: {

            },

            userGame: {},



        }
    },
    methods: {
        openSocket() {
            if(typeof(WebSocket) == "undefined") {
                alert("您的浏览器不支持WebSocket");
            }else{

                if (!app.userId) {
                    message("请填写昵称");
                    return;
                }

                //实现化WebSocket对象，指定要连接的服务器地址与端口  建立连接
                var socketUrl = host + "/mahjong/" + app.userId;
                socketUrl = socketUrl.replace("https","ws").replace("http","ws");
                if(socket!=null){
                    app.disconnect();
                }
                socket = new WebSocket(socketUrl);
                //打开事件
                socket.onopen = function() {

                };

                //获得消息事件
                socket.onmessage = function(msg) {
                    var resp = getResponse(msg.data);
                    if (resp.type == RESPONSE_TYPE.CONNECT) {
                        app.handleConnect(resp);
                    } else if (resp.type == RESPONSE_TYPE.ROOM_LIST) {
                        app.handleRoomList(resp)
                    } else if (resp.type == RESPONSE_TYPE.CREATE_ROOM) {
                        app.handleCreateRoom(resp);
                    } else if (resp.type == RESPONSE_TYPE.JOIN_ROOM) {
                        app.handleJoinRoom(resp);
                    } else if (resp.type == RESPONSE_TYPE.START_GAME || resp.type == RESPONSE_TYPE.FIRE) {
                        app.handleStartGame(resp);
                    } else if (resp.type == RESPONSE_TYPE.DISPATCH) {
                        app.handleDispatch(resp);
                    }
                };
                //关闭事件
                socket.onclose = function() {
                    console.log("websocket已关闭");
                };
                //发生了错误事件
                socket.onerror = function() {
                    console.log("websocket发生了错误");
                }
            }
        },

        handleConnect(resp) {
            if (resp.code != 200) {
                message(resp.message);
            } else {
                app.connected = true;
                app.getRoomList();
            }
        },

        send(request) {
            if (request && request.type) {
                socket.send(JSON.stringify(request))
            }
        },

        disconnect() {
            socket.close();
            socket=null;
            app.connected = false;
        },

        getRoomList() {
            app.userRequest = {};
            app.userRequest.type = REQUEST_TYPE.ROOM_LIST;
            app.send(app.userRequest);
        },

        handleRoomList(data) {
            if (data.code != 200) {
                message(data.message);
            } else {
                app.roomList = data.data;
            }
        },

        createNewRoom() {
            app.userRequest = {};
            app.userRequest.type = REQUEST_TYPE.CREATE_ROOM;
            app.send(app.userRequest);
        },

        joinRoom(val) {
            app.userRequest = {};
            app.userRequest.type = REQUEST_TYPE.JOIN_ROOM;
            app.userRequest.roomId = val;
            app.send(app.userRequest);
        },

        handleCreateRoom(data) {
            if (data.code != 200) {
                message(data.message);
            } else {
                app.joinRoom(data.data);
            }
        },

        handleJoinRoom(data) {
            if (data.code != 200) {
                message(data.message);
            } else {
                app.room = data.data;
            }
        },

        quitRoom() {
            app.userRequest = {};
            app.userRequest.type = REQUEST_TYPE.QUIT_ROOM;
            app.send(app.userRequest);
            app.room = {}
        },

        handleStartGame(data) {
            if (data.code != 200) {
                message(data.message);
            } else {
                app.userGame = data.data;
                app.started = true;
                if (data.data.myTurn && !data.data.hasOperation) {
                    app.dispatch();
                }
            }
        },

        getCardClass(cardName) {
            var classes = []
            if (cardName.indexOf("万") > -1) {
                classes.push("wan");
            }
            if (cardName.indexOf("条") > -1) {
                classes.push("tiao");
            }
            if (cardName.indexOf("饼") > -1) {
                classes.push("bing");
            }
            if (cardName == app.userGame.baida) {
                classes.push("baida");
            }
            return classes.join(" ");
        },

        startGame() {
            app.userRequest = {};
            app.userRequest.type = REQUEST_TYPE.START_GAME;
            app.userRequest.roomId = app.room.roomId;
            app.send(app.userRequest);
        },

        dispatch() {
            app.userRequest = {};
            app.userRequest.type = REQUEST_TYPE.DISPATCH;
            app.send(app.userRequest);
        },

        handleDispatch(data) {
            if (data.code != 200) {
                message(data.message);
            } else {
                app.userGame = data.data;
                app.userGame.privateMahjongs = data.data.privateMahjongs.slice(0, data.data.privateMahjongs.length - 1)
            }
        },

        fire(name) {

            if(!app.userGame.myTurn) {
                app.$message('未到您打牌');
                return;
            }

            if (app.userGame.hasOperation || app.userGame.myOperation) {
                app.$message("有特殊操作，无法打牌")
                return;
            }

            app.userRequest = {};
            app.userRequest.type = REQUEST_TYPE.FIRE;
            app.userRequest.mahjong = name;
            app.send(app.userRequest);

        },

    }
});

function message(msg) {
    app.$message(msg);
}

function getResponse(msg) {
    if (msg) {
        return JSON.parse(msg);
    } else {
        return "";
    }
}