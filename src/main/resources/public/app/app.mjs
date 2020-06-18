import SideMenu from '/components/side-menu.mjs';
import MainContent from '/components/main-content.mjs';
import Vue from '/vendor/vue/vue.esm.browser.js';

new Vue({
    el: '#app',
    beforeMount() {
        this.self = this;
        Vue.component("windflow-layout", function (resolve, reject) {
            fetch('/api/layouts/sidebar-layout.html')
                .then((response) => response.text())
                .then((data) => resolve({
                    template: data,
                    components: {
                        SideMenu, MainContent
                    },
                    data() {
                        return {
                            menuArea: 'SideMenu',
                            contentArea: 'MainContent'
                        }
                    }
                }));
        })
    }
});
