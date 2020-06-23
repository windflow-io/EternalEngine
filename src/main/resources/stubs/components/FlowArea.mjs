import Vue from '/vendor/vue/vue.esm.browser.js';

export default {
    name: 'FlowArea',
    data() {
        return {
            areaComponents: []
        }
    },
    props: ['name'],
    template: '<div><component :key="component.id" v-for="component in areaComponents" :is="component.name"/></div>',
    beforeMount() {
        this.$store.dispatch('fetchPageData').then(page => {
            let pageComponents = page.components
            let components = pageComponents.find(e => e.area === this.name).components
            components.forEach(component => {
                this.areaComponents.push(component)
                Vue.component(component.name, function(resolve, reject){
                    import('/api/components/' + component.name + '.mjs').then(module => {
                        resolve(module);
                    })
                })
            });

        })
    }
}
