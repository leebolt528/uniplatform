import React from 'react';

const ErrorBody = ( props ) => {
  const { status, stack, message, info } = props;

  if( info ) {
    return (
      <div>
        <h1>{ info.heading }</h1>
        <p>{ info.summary }</p>
        <ul>
          {
            info.suggestions.map( ( suggestion ) => <li>{ suggestion }</li> )
          }
        </ul>
      </div>
    );
  } else {
    return (
      <div>
        <h1>{ message }</h1>
        <h2>{ status }</h2>
        <pre>{ stack }</pre>
      </div>
    );
  }
};

const Error = ( props ) => (
  <div { ...props.page }>
    <ErrorBody { ...props } />
  </div>
);

export default Error;