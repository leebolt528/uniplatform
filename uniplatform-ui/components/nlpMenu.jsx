import React, { Component } from 'react';
import { Menu} from 'epm-ui';

class NLPMenu extends Component {
    constructor( props ) {
        super( props );
        this.state = {
            menu : []
        }
    }

    componentDidMount(){
        this.fetchMenu();
    }
    fetchMenu(){
        fetch(Uniplatform.context.url + '/nlap/admin/menu/mgmt/menu/list', {
            credentials: 'same-origin',
            method: 'GET'
        })
        .then((res) => res.json())
        .then((data) => {
            let menu = data.menu;
            this.setState({menu});
            this.showMenu();
        }).catch((err) => console.log(err.toString()));
    }

    showMenu(){
        let src = ['../image/2.png','../image/3.png','../image/4.png'];
        let {menu} = this.state;
        let menuHtml = [];
        let link = this.props.url;

        for(let i=0; i<menu.length; i++){
            let flag = false;
            let item = menu[i];
            let menuChi = [];
            for(let j=0; j<item.children.length; j++){
                let chi = item.children[j];
                let color = false;
                if(chi.link == link){
                    flag = true;
                    color = true;
                }
                let m = [];
                if(color){
                    m.push(
                        <li style={{backgroundColor:'#f2f2f2'}}>
                            <a href={chi.link} key={chi.link}>{chi.title}</a>
                            {/*<a key={chi.link}>{chi.title}</a>*/}
                        </li>
                    );
                }else {
                    m.push(
                        <li onClick={()=>{this.props.href(chi.link)}}>
                            <a href={chi.link} key={chi.link}>{chi.title}</a>
                        </li>
                    );
                    /*m.push(
                        <li onClick={()=>{this.props.href(chi.link)}}>
                            <a key={chi.link}>{chi.title}</a>
                        </li>
                    );*/
                }
                menuChi.push(m);
            }

            if(flag){
                menuHtml.push(
                    <li key={item.title}>
                        <div className="link" onClick={this.clickFirstMenu.bind(this,i)}>
                            <img src={src[i]} />{item.title}
                            <i className="fa fa-chevron-down"></i>
                        </div>
                        <ul className="submenu">
                            {menuChi}
                        </ul>
                    </li>
                );
            }else {
                menuHtml.push(
                    <li key={item.title}>
                        <div className="link" onClick={this.clickFirstMenu.bind(this,i)}>
                            <img src={src[i]} />{item.title}
                            <i className="fa fa-chevron-down"></i>
                        </div>
                        <ul className="submenu" style={{display:'none'}}>
                            {menuChi}
                        </ul>
                    </li>
                );
            }
        }
        return menuHtml;
    }
    clickFirstMenu(index){
        let link = document.getElementsByClassName('link');
        let submenu = document.getElementsByClassName('submenu');
        for(let i=0; i<submenu.length; i++){
            if(index != i){
                submenu[i].style.display = 'none';
            }
        }
        if(submenu[index].style.display == 'none'){
            submenu[index].style.display = 'inline';
        }else {
            submenu[index].style.display = 'none';
        }
    }

    render() {
        let menu = this.state.menu;
        return (
            <div className="account-l fl">
                <ul id="accordion" className="accordion">
                    {this.showMenu()}
                </ul>
            </div>
        );
    }
}

export default NLPMenu;