export default {
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
    template: '<div>{{name}}<br/><component :key="component.id" v-for="component in areaComponents" :is="component.name"/></div>',
}
