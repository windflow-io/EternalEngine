import Vue from '/vendor/vue/vue.esm.browser.js';

export default {
    name: 'FlowArea',
    data() {
        return {
            areaComponents: [],
            host:window.location.host,
            path:window.location.pathname
        }
    },
    props: ['name'],
    template: '<div>{{name}}<br/><component :key="component.id" v-for="component in areaComponents" :is="component.name"/></div>',
    beforeMount() {
        this.$store.dispatch('fetchPageData', {host:this.host, path:this.path}).then(page => {
            
            let components = page.components.find(e => e.area === this.name).components
            components.forEach(component => {
                this.areaComponents.push(component)
                Vue.component(component.name, function(resolve, reject){
                    import('/api/components/' + component.name + '.mjs').then(module => {
                        resolve(module);
                    })
                })
            });
        });
    }
}
