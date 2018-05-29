const express = require( 'express' );
const router  = express.Router();

router.get( '/corpus/index', ( req, res, next ) => {
  res.render( 'corpus/index', {
    page: {
      title: 'platform'
    }
  } );
} );

module.exports = router;