import Vue from '/vendor/vue/vue.esm.browser.js';

export default {
    name: "Windflow",
    data() {
        return {
            rootComponent: null
        }
    },
    template: '<component :is="rootComponent"/>',
    async beforeMount() {
        const module = await import('/api/components/hello-world.mjs')
        const rawComponent = module.default;
        const rootComponent = rawComponent.name;
        Vue.component(rootComponent, rawComponent);
        this.rootComponent = rootComponent;
    }
}
