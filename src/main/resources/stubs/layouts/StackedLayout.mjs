import FlowArea from '/api/components/FlowArea.mjs'
export default {
    name: 'LeftMenuLayout',
    components: {FlowArea},
    template:
        `<section class="flex flex-row h-screen bg-gray-900">
            <div class="flex-grow bg-blue-500 bg-opacity-25 p-1 text-white"><flow-area name="main"></flow-area></div>
        </section>`,
}
