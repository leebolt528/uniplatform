const express = require( 'express' );
const router  = express.Router();

/* GET home page. */
router.get( '/', ( req, res, next ) => {
  res.redirect( req.app.locals.site.contextPath + '/common/index' );
} );

router.get( '/index', ( req, res, next ) => {
  res.render( 'common/index', {
    page: {
      title: 'platform'
    }
  } );
} );

module.exports = router;