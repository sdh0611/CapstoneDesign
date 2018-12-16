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
	database		: 'GuestInfo'
})




//ID, PW를 비교하는 함수
var identifyID = function (loginType, userID, userPW, req, res){

	console.log("[Server] Input userID : " + userID + ", Input userPW : " + userPW);
			
	//Get connection from connectionPool
	pool.getConnection((err, connection)=>{
		if(err)
			console.log("[Server] err : ", err);
		else{
			//sql query 실행
			console.log("[Server] Finding...");
			var sql = "";
			if(loginType == "personal")
			{
				sql = "select * from PersonalUserInfo where PERSONAL_ID='" + userID + "'"
			}
			else if(loginType == "business")
			{
				sql = "select * from BusinessUserInfo where BUSINESS_ID='" + userID + "'"
			}

			connection.query(sql, (err, rows, fields) => {
				console.log("[Server] " + sql);
				if(err){
					//DB내에 일치하는 data가 없는 경우
					console.log("[Server] Error");
					res.write("fail", (err)=>{
						console.log("[Server] Send fail");
						res.end();		
					});	
					throw err;
				}
				else{
					if(!rows.length)
					{
						console.log("[Server] No data");
						res.write("fail", (err)=>{
							console.log("[Server] Send fail");
							res.end();		
						});	
						return;
					}
					
					//PW 비교
					var pw = "";
					for(var i in rows){
						if(loginType == "personal")
						{
							pw = rows[i].PERSONAL_PW;
						}
						else if(loginType == "business")
						{
							pw = rows[i].BUSINESS_PW;
						}

						if(pw != userPW){
							console.log("[Server] Password Missmatch!");
							console.log("[Server] Fail");
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
		console.log("[Server] Connection Release");
		connection.release();
	});	

}

var registPersonal = function(userID, userPW, name, age, mailAddr, req, res)
{
	console.log("[Server] Input userID : " + userID + ", Input userPW : " + userPW
					+ ", Input name : " + name + ", Input age : " + age + ", Input MailAddr : " + mailAddr);
			
	//Get connection from connectionPool
	pool.getConnection((err, connection)=>{
		if(err)
			console.log("[Server] err : ", err);
		else{
			//sql query 실행
			var sql = "insert into PersonalUserInfo(PERSONAL_ID, PERSONAL_PW, PERSONAL_NAME, PERSONAL_AGE, PERSONAL_MAIL)"
			+ " values('" + userID + "', '" + userPW + "', '" + name + "', " + age + ", '" + mailAddr + "')";
			connection.query(sql, (err, rows, fields) => {
				if(err){
					//Insert 실패
					console.log("[Server] Regist fail");
					res.write("fail", (err)=>{
						console.log("[Server] Send fail");
						res.end();
					});
				}
				else{
					console.log("[Server] Regist Success!!");
					res.write("success", (err)=>{
						console.log("[Server] Send success");
						res.end();		
					});
					
				}
			});
		}
		connection.release();
	});	
}

var registBusiness = function(userID, userPW, name, part, mailAddr, req, res)
{
	console.log("[Server] Input userID : " + userID + ", Input userPW : " + userPW
					+ ", Input name : " + name + ", Input Part : " + part + ", Input MailAddr : " + mailAddr);
			
	//Get connection from connectionPool
	pool.getConnection((err, connection)=>{
		if(err)
			console.log("[Server] err : ", err);
		else{
			//sql query 실행
			var sql = "insert into BusinessUserInfo(BUSINESS_ID, BUSINESS_PW, BUSINESS_NAME, BUSINESS_DEPARTMENT, BUSINESS_EMAIL)"
			+ " values('" + userID + "', '" + userPW + "', '" + name + "', '" + part + "', '" + mailAddr + "')";
			connection.query(sql, (err, rows, fields) => {
				console.log("[Server] " + sql);
				if(err){
					//Insert 실패
					console.log("[Server] Regist fail");
					res.write("fail", (err)=>{
						console.log("[Server] Send fail");
						res.end();
					});
				}
				else{
					console.log("[Server] Regist Success!!");
					res.write("success", (err)=>{
						console.log("[Server] Send success");
						res.end();		
					});
					
				}
			});
		}
		connection.release();
	});	

}


// app.get('/users', (req, res)=>{
// 	console.log('who get in here/users');
// 	var userID = req.query.id;
// 	var userPW = req.query.pw;

// 	if(userID && userPW){
// 		findID(userID, userPW);
// 	}
// });

//Post
app.post('/post', (req, res)=>{
	console.log('[Server] Who get in here post /users');
	var inputData;

	//Client로부터 받은 Json 데이터 pasing
	req.on('data', (data)=>{
		inputData = JSON.parse(data);
	});

	//parsing한 데이터와 db내의 데이터 대조 및 결과 client 전송
	req.on('end', ()=>{

		var type = inputData.type;
		console.log(type);
		if(type == 'identify')
		{
			console.log("Identify!");	
			var loginType 	= inputData.loginType;		
			var userID 		= inputData.userID;
			var userPW 		= inputData.userPW;

			//userID와 userPW가 유효한 경우
			if(loginType && userID && userPW){
				identifyID(loginType, userID, userPW, req, res);
			}
			else
			{
				console.log("[Server] Invalid Value!!");
				res.write("fail", (err)=>{
					console.log("[Server] Send fail");
					res.end();
				});
			}
		}
		else if(type == 'registPersonal')
		{
			var userID 			= inputData.userID;
			var userPW 			= inputData.userPW;
			var userName 		= inputData.userName;
			var userAge 		= inputData.userAge;
			var userMailAddr 	= inputData.userMailAddr;
			console.log("RegistBusiness!");	
			registPersonal(userID, userPW, userName, userAge, userMailAddr, req, res);
		}
		else if(type == 'registBusiness')
		{
			var userID 			= inputData.userID;
			var userPW 			= inputData.userPW;
			var userName 		= inputData.userName;
			var userPart 		= inputData.userPart;
			var userMailAddr 	= inputData.userMailAddr;
			console.log("RegistBusiness!");	
			registBusiness(userID, userPW, userName, userPart, userMailAddr, req, res);
		}
		else
		{
			console.log("[Server] Invalid Value!!");
			res.write("fail", (err)=>{
				console.log("[Server] Send fail");
				res.end();
			});
		}

	});

})

app.listen(3000, ()=>{
	console.log("Server running on 3000...");

})
