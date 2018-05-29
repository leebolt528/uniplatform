const header = require( 'gulp-header' );
const path   = require( 'path' );

const pkg    = require( path.join( process.cwd(), 'package.json' ) );
const config = require( '../config/settings' );

module.exports = () => header( config.banner, { pkg : pkg, time: Date.now() } );