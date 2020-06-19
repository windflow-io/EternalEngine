import Vue from '/vendor/vue/vue.esm.browser.js';

export default {
    async beforeMount() {
        let components = this.page.components;
        for (let index = 0; index < components.length; index++) {
            let area = components[index].area;
            let component = components[index].component

            const module = await import('/api/components/' + component + '.mjs')
            const rawComponent = module.default;
            const componentName = rawComponent.name;
            Vue.component(componentName, rawComponent);
            Vue.set(this.display, area, componentName);
        }
    }
}
