import {FlowArea} from '/modules/coreComponents.mjs'

export default {
    name: 'LeftMenuLayout',
    components: {FlowArea},
    template:
        `<section class="flex flex-row h-screen bg-gray-900">
            <div class="w-64 bg-purple-500 bg-opacity-25 p-1 text-white"><flow-area name="left"></flow-area></div>
            <div class="flex-grow bg-blue-500 bg-opacity-25 p-1 text-white"><flow-area name="right"></flow-area></div>
        </section>`,
}