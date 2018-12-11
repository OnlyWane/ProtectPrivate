var fs = require("fs");

exports.A_fun = function(req, res, next) {
    res.json({
        code: 200,
        message: '成功了'
    });
};

exports.read_csv = function(req, res, next) {

    fs.readFile('./public/files/ant-1.3.csv', function (err, data) {
        var table = new Array();
        if (err) {
            console.log(err);
            res.json({
                code: 500,
                message: err
            });
            return;
        }
        // console.log(data, '000000');
        ConvertToTable(data, function (table) {
            // console.log(table);
            res.json({
                code: 200,
                message: '成功了',
                data: table
            });
        })
    });

};

function ConvertToTable(data, callBack) {
    data = data.toString();
    var table = new Array();
    var rows = new Array();
    rows = data.split("\r\n");
    console.log(rows);
    for (var i = 0; i < rows.length; i++) {
        table.push(rows[i].split(","));
    }
    callBack(table);
}
