const koa = require('koa');
const fs = require('fs');
const app = koa();

app.use(function *(){
    var files = yield callFs(this);
    this.body = files.join('<br>');
});

console.log('listening on 3k');
app.listen(3000);


const callFs = function(resumer){
    fs.readdir('Pictures', function(e, files) {
        console.log(resumer);
        if(e){ console.log(e); throw e; }
        resumer.next(files);
    })
    return true;
};