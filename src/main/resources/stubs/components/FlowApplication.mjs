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
            host: window.location.host,
            path: window.location.pathname,
            currentPath: window.location.pathname
        }
    },
    computed: {
        layoutComponent() {
            return this.$store.state.pageLayout;
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
            this.currentPath = current;
            this.pageLoad(this.host, current);
        },
        pageLoad(host, path) {
            this.$store.dispatch('fetchPageData', {host:host, path:path});
        }
    }
}
