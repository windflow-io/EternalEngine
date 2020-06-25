import Vue from '/vendor/vue/vue.esm.browser.js';
import {addUrlListener} from '/modules/history.mjs'

export default {
    name: 'FlowApplication',
    template: `
        <div>
            <component :is="layoutComponent" :key="currentPath"/>          
        </div>
    `,
    data() {
        return {
            layoutComponent: null,
            host: window.location.host,
            path: window.location.pathname,
            currentPath: window.location.pathname
        }
    },
    created() {
        addUrlListener(this.urlChanged);
    },
    beforeMount() {
        this.pageLoad(this.host, this.path);
    },
    methods: {
        urlChanged(current, previous) {
            console.log (current, previous)
            this.currentPath = current;
            this.pageLoad(this.host, current);
        },
        pageLoad(host, path) {
            this.$store.dispatch('fetchPageData', {host:host, path:path}).then(page => {
                const layoutUrl = '/api/layouts/' + page.layout + '.mjs'
                import(layoutUrl).then((module)=>{
                    Vue.component(module.default.name, module.default);
                    this.layoutComponent = module.default.name;
                });
            });
        }
    }
}
