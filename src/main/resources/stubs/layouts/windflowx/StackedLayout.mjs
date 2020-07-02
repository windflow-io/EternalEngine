import {FlowArea} from '/modules/coreComponents.mjs'
export default {
    name: 'StackedLayout',
    components: {FlowArea},
    /**@TODO: Pull the themes from a Mixin from the Vue store **/
    template:
        `<section class="flex flex-row h-screen" :class="[backgroundColor, foregroundColor]" >
            <div class="flex-grow bg-blue-500 bg-opacity-25 p-1"><flow-area name="main"></flow-area></div>
        </section>`,
    data() {
        return {
            backgroundColor: Math.round(Math.random()) === 1 ? 'bg-gray-900' : 'bg-red-900',
            foregroundColor: Math.round(Math.random()) === 1 ? 'text-yellow-500' : 'text-green-500'
        }
    }
}
