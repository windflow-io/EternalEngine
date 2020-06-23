import Vue from '/vendor/vue/vue.esm.browser.js';

export default {
    name: 'FlowArea',
    data() {
        return {
            myComponents: []
        }
    },
    props: ['name'],
    template: '<div><component v-for="component in myComponents" :is="component"/></div>',
    beforeMount() {
        let pageComponents = this.$store.state.pageComponents
        let componentNames = pageComponents.find(e => e.area === this.name).components
        self = this;
        componentNames.forEach(componentName => {
            this.myComponents.push(componentName)
            Vue.component(componentName, function(resolve,reject){
                import('/api/components/' + componentName + '.mjs').then(module => {
                    resolve(module);
                })
            })
        });
    }
}
