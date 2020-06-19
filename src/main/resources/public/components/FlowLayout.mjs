import Vue from '/vendor/vue/vue.esm.browser.js';
import FlowArea from '/components/FlowArea.mjs'
//import {loadComponent} from "../modules/windflowUtils";
//import {loadComponent} from "../modules/windflowUtils";

export default {
    name: 'FlowLayout',
    template: '',
    //components: {FlowArea},
    beforeMount() {
        self = this
        console.log("way before");
        Vue.component('FlowArea', function (resolve, reject) {
            fetch('/api/pages/' + document.location.href).then((response) => response.text()).then(pageData => {
                console.log(pageData);
                resolve(FlowArea);
            })
        })
        //         //self.$store.commit("setPageMeta", pageData.metaData);
        //         //self.$store.commit("setPageComponents", pageData.components);
        //         //self.loadPageComponents(resolve, reject);
        //         resolve();
        //     })
        // })
    },
    methods: {
        loadPageComponents(resolve, reject) {
//            resolve();
        }
    },
    mounted() {
        console.log ("Flow Layout Mounted");
    }
}
