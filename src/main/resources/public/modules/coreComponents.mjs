import {addUrlListener, removeNamespace, mapQueryString} from '/modules/windflowUtils.mjs'

export const FlowApplication = {
    name: 'FlowApplication',
    template: `
        <div>
            <component :is="removeNamespace(layoutComponent)" :key="currentPath"/>          
        </div>
    `,
    data() {
        return {
            host:  mapQueryString(window.location.href).host || location.host,
            path: location.pathname,
            currentPath: location.pathname
        }
    },
    computed: {
        layoutComponent() {
            return this.$store.state.pageLayout;
        },
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
        },
        removeNamespace: removeNamespace
    },
}

export const FlowArea = {
    name: 'FlowArea',
    data() {
        return {
            host:window.location.host,
            path:window.location.pathname
        }
    },
    props: ['name'],
    computed: {
        areaComponents() {
            const area = this.$store.state.pageComponents.find(({ area }) => area === this.name);
            return area ? area.components : [];
        }
    },
    methods: {
        /**@TODO: shove this in it's own place **/
        removeNamespace: removeNamespace
    },
    template: '<component :key="component.id" v-for="component in areaComponents" :is="removeNamespace(component.name)"/>',
}