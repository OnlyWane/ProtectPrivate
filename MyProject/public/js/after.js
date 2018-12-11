function sendCompany() {
    window.location.href = 'IPR.html';
    // body...
}
function getData04() {
    // body...	
	let fileName =  localStorage.getItem('comany_name') + '_hide';
	let page = 1;
	// 发起请求
	// POST请求
    var req = new XMLHttpRequest();
    // OPEN
    req.open('POST', '/hello_jk/read_csv');
	// req.open('POST', '/user/create_user');
    // 设置请求头部信息
    req.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    // 指定服务端返回的数据类型
    req.responseType = 'json';
    // 发起请求
    req.send('fileName=' + fileName + '&page=' + page);
    // 添加监听事件
    req.onload = function (e) {
    if (this.status === 200) {
        // 获取服务端返回的数据
        // console.log(this, "9999");
        if (this.response.code === 200) {
    		console.log(this.response, "000000");
    		var data = this.response.data;
            for (var i = 0; i < data.length; i++) {
                var row = document.createElement('tr');
                var row_ele = document.getElementById('table4');
                row_ele.appendChild(row);
                for (var j = 0; j < data[i].length/3; j++) {
                    var cell = document.createElement('td');
                    var reg = new RegExp('"',"g"); 
    				let str = data[i][j].replace(reg, "");
    	            cell.innerHTML = str;
    	            row.appendChild(cell);	
                    }
                }
                for (var i = 0; i < data.length; i++) {
                    var row = document.createElement('tr');
                    var row_ele = document.getElementById('table5');
                    row_ele.appendChild(row);
                    for (var j = data[i].length/3; j < data[i].length/3*2; j++) {
                    	var cell = document.createElement('td');
                    	var reg = new RegExp('"',"g"); 
    					let str = data[i][j].replace(reg, "");
    	                cell.innerHTML = str;
    	                row.appendChild(cell);	
                    }
                }
                for (var i = 0; i < data.length; i++) {
                    var row = document.createElement('tr');
                    var row_ele = document.getElementById('table6');
                    row_ele.appendChild(row);
                    for (var j = data[i].length/3*2; j < data[i].length; j++) {
                    	var cell = document.createElement('td');
                    	var reg = new RegExp('"',"g"); 
    					let str = data[i][j].replace(reg, "");
    	                cell.innerHTML = str;
    	                row.appendChild(cell);	
                    }
                }
    		} else {
    			console.log(this.response.code, 'error');
    		}
    	} else {
    		alert('网络错误');
    		// AddSpan("div_span", "网络错误");
    	}
	}
}
getData04();