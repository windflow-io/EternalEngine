import {FlowArea} from '/modules/coreComponents.mjs'
export default {
    name: 'StackedLayout',
    components: {FlowArea},
    /**@TODO: Pull the themes from a Mixin from the Vue store **/
    template:
        `<section class="">
            <div><flow-area name="main"></flow-area></div>
        </section>`,

}
