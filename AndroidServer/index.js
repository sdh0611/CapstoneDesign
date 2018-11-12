const express 	= require('express');
const app		= express();
var mysql		= require('mysql');

//Init ConnectionPool
//MySQL 접근Port는 기본 3306번 사용
var pool = mysql.createPool({
	connectionLimit : 10,
	host			: 'localhost',
	user			: 'root',
	password		: 'xowkd5481@',
	database		: 'Study'
})


app.get('/users', (req, res)=>{
	console.log('who get in here/users');
	var userID = req.query.id;
	var userPW = req.query.pw;

	if(userID && userPW){
		console.log("id : " + userID + ", pw : " + userPW);
		pool.getConnection((err, connection)=>{
			if(err)
				console.log("err : ", err);
			else{
				var sql = "select * from user where userID='" + userID + "'";
				connection.query(sql, (err, rows, fields) => {
					if(err){
						console.log("err : " + err);
					}
					else{
						if(rows=="")
						{
							console.log("No data");
							res.send("No data...");
							return;
						}

						var pw = "";
						pw = rows[0].userPW;
						console.log("In sql pw : " + rows[0].userPW);
						if(pw != req.query.pw){
							console.log("Password Missmatch!");
							res.send("PW MisMatch");
						}
						else
						{
							console.log("Collect!");
							res.send("Collect PW!");
						}
					}
				});
			}
			connection.release();
		});	
	}
});

//Post
app.post('/post', (req, res)=>{
	console.log('[Server] Who get in here post /users');
	var inputData;

	//Client로부터 받은 Jso 데이터 pasing
	req.on('data', (data)=>{
		inputData = JSON.parse(data);
	});

	//pasing한 데이터와 db내의 데이터 대조 및 결과 client 전송
	req.on('end', ()=>{
		var userID = inputData.userID;
		var userPW = inputData.userPW;

		//userID와 userPW가 유효한 경우
		if(userID && userPW){
			console.log("[Server] Input userID : " + userID + ", Input userPW : " + userPW);
			
			//Get connection from connectionPool
			pool.getConnection((err, connection)=>{
				if(err)
					console.log("[Server] err : ", err);
				else{
					//sql query 실행
					var sql = "select * from user where userID='" + userID + "'";
					connection.query(sql, (err, rows, fields) => {
						if(err){
							//DB내에 일치하는 data가 없는 경우
							console.log("[Server] No data");
							res.write("fail", (err)=>{
								console.log("[Server] Send fail");
								res.end();		
							});
						}
						else{
							var pw = "";
							for(var i in rows){
								pw = rows[i].userPW;
								if(pw != userPW){
									console.log("[Server] Password Missmatch!");
									res.write("fail", (err)=>{
										console.log("[Server] Send fail");
										res.end();		
									});
									
								}
								else
								{
									console.log("[Server] Collect!!");
									res.write("success", (err)=>{
										console.log("[Server] Send success");
										res.end();		
									});
								}
							}
						}
					});
				}
				connection.release();
			});	
		}
	});

})

app.listen(3000, ()=>{
	console.log("Server running on 3000...");

})
