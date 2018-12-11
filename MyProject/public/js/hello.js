function getData() {
	// GET请求
	var req = new XMLHttpRequest();
	// OPEN
	req.open('GET', '/hello_jk/hello_fun');
    // req.open('POST', '/user/create_user');
	// 设置请求头部信息
	req.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
	// 指定服务端返回的数据类型
	req.responseType = 'json';
	// 发起请求
	req.send();

	// 添加监听事件
	req.onload = function (e) {
		if (this.status === 200) {
			// 获取服务端返回的数据
			console.log(this, "9999");
			if (this.response.code === 200) {
                console.log(this.response, "000000");
            } else {
			}
        } else {
            alert('网络错误');
            // AddSpan("div_span", "网络错误");
        }
    }
}

getData();



function getData02() {
	// GET请求
	var req = new XMLHttpRequest();
	// OPEN
	req.open('GET', '/hello_jk/read_csv');
    // req.open('POST', '/user/create_user');
	// 设置请求头部信息
	req.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
	// 指定服务端返回的数据类型
	req.responseType = 'json';
	// 发起请求
	req.send();

	// 添加监听事件
	req.onload = function (e) {
		if (this.status === 200) {
			// 获取服务端返回的数据
			console.log(this, "read1111");
			if (this.response.code === 200) {
                console.log(this.response, "read000");
                if (this.response.code === 200) {
	                console.log(this.response, "read000");
	                
	                var data = this.response.data;

	                for (var i = 0; i < data.length-1; i++) {

	                	var row = document.createElement('tr');
	                	var row_ele = document.getElementById('table');
	                	row_ele.appendChild(row);

	                	for (var j = 0; j < data[i].length; j++) {
	                		var cell = document.createElement('td');
	                		console.log(data[i][j]);
		                	cell.innerHTML = data[i][j];
		                	row.appendChild(cell);
	                	}
	                }

    			}
			} else {
				alert('后端错误');
			}
		} else {
			alert('网络错误');
		}
	}
}

getData02();

 