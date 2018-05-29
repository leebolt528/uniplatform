import React, { Component } from 'react';
import { Page, Row, Col, Menu } from 'epm-ui';

class ProductMenu extends Component{
    constructor( props ) {
        super( props );
        this.state = {
            menu : []
        }

        this.handleSelect = this.handleSelect.bind( this );
    }

    componentDidMount(){
        this.fetchMenu();

    }

    fetchMenu(){
        fetch(Uniplatform.context.url + '/nlap/admin/function/display/menu/mgmt/menu/list', {
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
        let {menu} = this.state;
        let menuHtml = [];
        for(let i=0; i<menu.length; i++){
            if(menu[i].secondMenu.length == 0){
                menuHtml.push(
                    <li key={menu[i].url}>
                        <div className="link">
                            <img src={menu[i].imgPath} />
                            <a style={{textDecoration:'none',color:'#57585a',display:'block'}}
                               href={menu[i].url}>{menu[i].functionName}</a>
                        </div>
                    </li>
                );
            }else {
                let secondHtml = [];
                for(let j=0;j<menu[i].secondMenu.length;j++){
                    let second = menu[i].secondMenu;
                    secondHtml.push(
                        <li key={second[j].secondMenuUrl}><a href={second[j].secondMenuUrl}>{second[j].secondMenuName}</a></li>
                    );
                }
                menuHtml.push(
                    <li key={menu[i].url}>
                        <div className="link" onClick={this.showSubMenu.bind(this)}><img src={menu[i].imgPath} />
                            <span style={{color:'#57585a'}}>{menu[i].functionName}</span>
                            <i className="fa fa-chevron-down"></i>
                        </div>
                        <ul className="submenu" style={{display:'none'}}>{secondHtml}</ul>
                    </li>
                );
            }
        }
        this.setState({menuHtml});
        this.handleSelect();
    }

    handleSelect() {
        let link = this.props.url;
        let alink = document.getElementsByTagName('a');
        for(let i=0; i<alink.length; i++){
            if(alink[i].pathname == link){
                alink[i].parentNode.style.backgroundColor = '#f2f2f2';
            }
        }
        let flag = (link == '/nlap/product/textCategorization') ||
            (link == '/nlap/product/userFile') ||
            (link == '/nlap/product/rulesClassification');
        if(flag){
            let submenu = document.getElementsByClassName('submenu');
            submenu[0].style.display = 'inline';
        }
    }

    showSubMenu(){
        let submenu = document.getElementsByClassName('submenu');
        if(submenu[0].style.display == 'none'){
            submenu[0].style.display = 'inline';
        }else {
            submenu[0].style.display = 'none';
        }
    }

    render() {
        let {menuHtml} = this.state;
        return (
            <div className="account-l fl">
                <ul id="accordion" className="accordion">
                    {menuHtml}
                </ul>
            </div>
        );
    }
}

export default ProductMenu;

