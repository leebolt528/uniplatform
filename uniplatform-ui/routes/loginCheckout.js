const request = require( 'request' );

const ips = require( '../config/application.json' );
const url = 'http://' + ips.uniplatform.ip + ':' + ips.uniplatform.port + '/' + ips.uniplatform.name + '/nlap/admin/user/loginfo';

module.exports = (req, res, next) => {
    // console.log('********************************');
     console.log(req.session);

    if (req.session.sess) {

        request(
            {
                method: 'POST',
                url: url,
                form: {},
                headers: {
                    Cookie: req.session.sess
                }
            },
            ( error, response, body ) => {
                console.log( "*****************Has logged in******************" );

                if (body && JSON.parse(body).code === 1) {
                    next();
                }else {
                    res.redirect( req.context.contextPath + '/nlap/index' );
                }

            } );

    }else {
        res.redirect( req.context.contextPath + '/nlap/index' );
    }
};