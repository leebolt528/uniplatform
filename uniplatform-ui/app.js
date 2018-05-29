const express = require('express');
const favicon = require('serve-favicon');
const bodyParser = require('body-parser');
const proxy = require('http-proxy-middleware');

const path = require('path');

// configs
const errors = require(path.join(__dirname, 'config', 'errors.json'));
const compile = require(path.join(__dirname, 'config', 'compile.json'));
const context = require(path.join(__dirname, 'config', 'context.json'));
const application = require(path.join(__dirname, 'config', 'application.json'));

const reactExpress = require('epm-ui-express-integration');
reactExpress.options({
    main: __dirname + '/build/frame/html',
    context: context
});

// routes
const common = require('./routes/common');
// const corpus  = require( './routes/corpus' );
const dcci = require('./routes/dcci');
const nlap = require('./routes/nlap');
// const portals = require( './routes/portals' );
const usou = require('./routes/usou');

const app = express();

// view engine setup
// app.set( 'views', path.join( __dirname, compile.output.views ) );
// app.set( 'view engine', 'js' );
// app.engine( 'js', reactViews.createEngine() );
app.set('views', path.join(__dirname, compile.output.views));
app.set('view engine', 'js');
app.engine('js', reactExpress.engine());
app.use(reactExpress.context());

// Proxy /uniplatform requests to ip:port
const proxyOptions = {
    // target: 'http://172.16.11.43:9107',
    target: "http://" + application.uniplatform.ip + ":" + application.uniplatform.port,
    changeOrigin: true
};
const platformProxy = proxy(proxyOptions);
app.use('/uniplatform/*', platformProxy);

//app.use( favicon( path.join( __dirname, 'public', 'common', 'image', 'favicon.ico') ) );
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(express.static(path.join(__dirname, 'build/public')));
app.use('/views', express.static(path.join(__dirname, compile.output.views)));
app.use('/components', express.static(path.join(__dirname, compile.output.components)));
app.use('/bower_components', express.static(path.join(__dirname, compile.output.bower)));

app.use('/', common);
app.use('/common', common);
// app.use( '/corpus', corpus );
app.use('/dcci', dcci);
app.use( '/nlap', nlap );
// app.use( '/portals', portals );
app.use('/usou', usou);

// catch 404 and forward to error handler
app.use((req, res, next) => {
    let err = new Error('Not Found');
    err.status = 404;
    next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
    app.use((err, req, res, next) => {
        const status = err.status || 500;

        res.status(status);
        res.render('common/error', {
            page: {
                title: '出错了！'
            },
            status: status,
            message: err.message,
            stack: err.stack
        });
    });
}

// production error handler
// no stacktraces leaked to user
app.use((err, req, res, next) => {
    res.status(err.status || 500);
    res.render('common/error', {
        page: {
            title: '出错了！'
        },
        info: errors[err.status] || errors['default'],
        message: err.message
    });
});

module.exports = app;