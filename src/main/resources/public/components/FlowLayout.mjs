import Vue from '/vendor/vue/vue.esm.browser.js';
import FlowArea from '/components/FlowArea.mjs'

export default {
    name: 'FlowLayout',
    template: '',
    data() {
        return {
            componentCount: 0,
            componentsLoaded: 0
        }
    },
    beforeMount() {
        self = this;
        Vue.component('FlowArea', function (resolve, reject) {
            fetch('/api/pages/' + document.location.href).then((response) => response.json()).then(pageData => {
                self.$store.commit("setPageMeta", pageData.metaData);
                self.$store.commit("setPageComponents", pageData.components);
                resolve(FlowArea)
            })
        })
    },
    mounted() {
        console.log ("Flow Layout Mounted");
    }
}
