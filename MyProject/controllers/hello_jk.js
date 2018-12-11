var fs = require("fs");

exports.read_ipr = function(req, res, next) {
    let fileName = '';
    if(req.body.fileName) {
        fileName = `./public/files/${req.body.fileName}.csv`
    } else {
        res.json({
            code: 100,
            message: 'fileName是不可缺少的参数'
        });
        return ;
    }

    fs.readFile(fileName, function (err, data) {
        var table = new Array();
        if (err) {
            res.json({
                code: 101,
                message: '读取失败'
            });
            return ;
        }
        ConvertToTable(data, function (table) {
            res.json({
                code: 200,
                message: 'success',
                data: table[0] ? table[0] : [],
                length: table[0].length
            })
        })
    });

};

exports.read_csv = function(req, res, next) {
    let fileName = '';
    let page = req.body.page ? req.body.page : 1;
    if(req.body.fileName) {
        fileName = `./public/files/${req.body.fileName}.csv`
    } else {
        res.json({
            code: 100,
            message: 'fileName是不可缺少的参数'
        });
        return ;
    }
    fs.readFile(fileName, function (err, data) {
        var table = new Array();
        if (err) {
            res.json({
                code: 101,
                message: '读取失败'
            });
            return ;
        }
        ConvertToTable(data, function (table) {
            let tableData = [];
            let tableTitle = table[0];
            table.shift();
            let len = table.length;  
            if(table[len - 1].length == 1) {
                table.pop();
                len = table.length;
            }
            if(page > Math.ceil(len/20)) {
                res.json({
                    code: 102,
                    message: 'page超出最大限制'
                });
                return ;
            }
            tableData = table.slice((page - 1) * 20, page * 20);
            tableData.unshift(tableTitle);
            res.json({
                code: 200,
                message: '成功',
                data: tableData,
                len: table.length,
                total: Math.ceil(len/20)
            });
        })
    });

};

function ConvertToTable(data, callBack) {
    data = data.toString();
    var table = new Array();
    var rows = new Array();
    rows = data.split("\r\n");
    for (var i = 0; i < rows.length; i++) {
        table.push(rows[i].split(","));
    }
    callBack(table);
}