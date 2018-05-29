import React, { Component } from 'react';
import { App } from '../../components/uniplatform-ui';
import { Page,Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'epm-ui';

const page = {
  title: 'Usou',
  css: [
    'css/index.min.css'
  ],
  js: [
    'js/index.min.js'
  ]
};

class IndexApp extends Component {

  constructor( props ) {
    super( props );

    this.state = { visible: false };
  }

  showModal() {
    this.setState( { visible: true } );
  }

  handleClose() {
    this.setState( { visible: false } );
  }

  render() {

    return (
      <Page>
        <Button type="primary" onClick={ this.showModal.bind( this ) }>显示第一个模态框</Button>
        <Modal visible={ this.state.visible } onClose={ this.handleClose.bind( this ) } >
          <ModalHeader>
            模态框头部
          </ModalHeader>
          <ModalBody>
            模态框内容...
          </ModalBody>
          <ModalFooter>
            <Button onClick={ this.handleClose.bind( this ) }>关闭</Button>
            <Button>按钮 2</Button>
          </ModalFooter>
        </Modal>
      </Page>
    );
  }

}

IndexApp.UIPage = page;
export default IndexApp;
