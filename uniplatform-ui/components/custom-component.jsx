import React, { Component } from 'react';
// import { Container, Input, Button} from 'uniplatform-ui';

class CustomComponent extends Component {

  constructor(props) {
    super(props);

    this.state = { items: [] };
  }

  render() {

    return (  
      <div>
        <h2>这里展示了使用ES6语法，组件导入、导出的两种方式。</h2>
      </div>
    );
  }
}

export default CustomComponent;
export { CustomComponent }