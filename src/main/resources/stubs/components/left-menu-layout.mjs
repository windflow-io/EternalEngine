import Vue from '/vendor/vue/vue.esm.browser.js';
import FlowArea from '/components/FlowArea.mjs'

export default {
    name: 'LeftMenuLayout',
    template:

        `<section class="flex flex-row h-screen bg-gray-900">
            <div class="w-64 bg-purple-500 bg-opacity-25 p-1 text-white"><flow-area name="left"></flow-area></div>
            <div class="flex-grow bg-blue-500 bg-opacity-25 p-1 text-white"><flow-area name="right"></flow-area></div>
        </section>`,

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
    }
}
