import React, { Component } from 'react';
import Layout from './layouts/default';

import resources from './config/resources.json';

const PAGE_ROOT = 'uniplatform-ui-content';

const initScriptTmpl = ( ctx, data ) => `
  if ( window.UniplatformPage ) {
  
    var Uniplatform = {
      context: ${ ctx },
      args: ${ data }.page
    };
    
    var container = document.getElementById( '${ PAGE_ROOT }' );
    var App = UniplatformPage.default || UniplatformPage;
    ReactDOM.render(
      React.createElement( App, ${ data } ),
      container
    );
  }
`.trim();

export default ( props ) => {
  const res = resources[ props.env ] || {};

  const context = props.context;
  const contextPath = context.contextPath;

  const page = props.component.UIPage || {};

  const initScript = initScriptTmpl( JSON.stringify( context ), props.data );

  return (
    <html lang="zh-cn">
      <head>
        <meta charSet="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
        <meta httpEquiv="X-UA-Compatible" content="IE=edge, chrome=1" />
        <title>{ page.title || 'UNIPLATFORM' }</title>
        <link rel="shortcut icon" href={ `${ contextPath }/common/image/favicon.ico` } />
        { res.css && res.css.map( ( css, index ) => <link key={ index } rel="stylesheet" href={ contextPath + css } /> ) }
        { page.css && page.css.map( ( css, index ) => <link key={ index } rel="stylesheet" href={ contextPath + css } /> ) }
      </head>
      <body>
        <Layout id={ PAGE_ROOT } contextPath={ contextPath }>
          { props.children }
        </Layout>
        { res.js && res.js.map( ( js, index ) => <script key={ index } src={ contextPath + js } /> ) }
        { page.js && page.js.map( ( js, index ) => <script key={ index } src={ contextPath + js } /> ) }
        { props.view ? <script src={ `${ contextPath }/views/${ props.view }.${ props.env === 'development' ? 'js' : 'min.js' }` } /> : null }
        <script dangerouslySetInnerHTML={ { __html: initScript } } />
      </body>
    </html>
  );

};

