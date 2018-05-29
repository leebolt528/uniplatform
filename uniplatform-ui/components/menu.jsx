import React, { Component } from 'react';
import { Menu } from 'epm-ui';

 const menu = [
    { title:'管控首页',link:'/dcci/platform/index'},
    { title:'站点管理',link:'/dcci/platform/site/list'},
    { title:'机器管理',link:'/dcci/platform/machine/list' },
    { title:'组件管理',link:'/dcci/platform/component/list' },
    { title:'需求管理',link:'/dcci/platform/require/list' },
    { title:'任务管理',link:'/dcci/platform/task/list' },
    { title:'商城管理',link:'/dcci/platform/data/list' }
  ];

class Com_Menu extends Component {
  constructor( props ) {
    super( props );
    this.state = {
      menu : menu
    }
       
    this.handleSelect = this.handleSelect.bind( this );
  }

  componentDidMount(){
    this.handleSelect();
  }

  handleSelect() {
    let link = this.props.url;
    let menu = this.state.menu;
    menu.map((item,index) => {
      if(item.link == link){
        menu[index]["selected"] = true;
      }
    });
    this.setState({ menu });
  }


  render() {
    let menu = this.state.menu;
    return (
      <div>
        <Menu dataSource={ menu } bordered />
      </div>
    );
  }

}

export default Com_Menu;
export {Com_Menu};