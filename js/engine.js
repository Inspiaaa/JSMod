

/*
Module:
{
	id: "", 	// Guaranteed
	name: "",	// Guaranteed
	desc: "",	// Guaranteed

	onTick: () => void,
	onRender: () => void,

	isActive: false,			// Guaranteed
	activeByDefault: false,		// Guaranteed
	activationKey: KEY_NONE,

	saveState: () => Object,
	loadState: (Object) => void
	cleanState: () => void
}
*/

function _createEngine() {
	"use strict";

	var self = {}

	function activateModule(module) {
		Chat.msg(Chat.GREEN + Chat.BOLD + "Activating " + module.name)
		module.isActive = true
		
		if ("onActivate" in module) {
			module.onActivate()
		}

		if ("onTick" in module) {
			onTickListeners.push(module.onTick)
		}
	}

	function deactivateModule(module) {
		Chat.msg(Chat.RED + "Deactivating " + module.name)
		module.isActive = false
		
		if ("onDeactivate" in module) {
			module.onDeactivate()
		}

		if ("onTick" in module) {
			removeItem(onTickListeners, module.onTick)
		}
	}

	function toggleModule(module) {
		if (module.isActive) {
			deactivateModule(module)
		}
		else {
			activateModule(module)
		}
	}
	
	function setModuleActive(module, isActive) {
		if (isActive != module.isActive) {
			toggleModule(module)
		}
	}

	function removeItem(array, value) {
		var index = array.indexOf(value);
		if (index > -1) {
			array.splice(index, 1);
		}
	}

	function setDefault(module, key, value) {
		if (! (key in module)) {
			module[key] = value
		}
	}

	var autoIdCounter = 0
	function loadModules() {
		modulesToLoad.forEach(func => {
			var module = func()

			module.isActive = false

			if (! ("id" in module)) {
				if ("name" in module) {
					module.id = module.name
				}
				else {
					module.id = "#" + autoIdCounter.toString()
				}
			}

			setDefault(module, "name", "Module")
			setDefault(module, "desc", "")
			setDefault(module, "activeByDefault", false)

			if ("activationKey" in module) {
				keyToActivate.set(module.activationKey, module)
			}

			if (module.activeByDefault) {
				activateModule(module)
			}

			modulesById.set(module.id, module)
			autoIdCounter ++
		});

		// Clear the list
		modulesToLoad = []
	}

	var modulesById = new Map()
	var modulesToLoad = []
	var onTickListeners = []
	// TODO: Add support for multiple modules with the same KeyBind
	// Map of KeyCodes (int) to Modules (Object)
	var keyToActivate = new Map()

	self.registerModule = function(func) {
		modulesToLoad.push(func)
	}

	// TODO: Create function to unregister module

	self.onTick = function() {
		var count = onTickListeners.length
		for (var i = 0; i < count; i++) {
			onTickListeners[i]()
		}
	}

	self.onKeyDown = function(keyCode) {
		if (keyToActivate.has(keyCode)) {
			toggleModule(keyToActivate.get(keyCode))
		}
	}

	self.onKeyUp = function(keyCode) { };

	self.onInit = function() {
		loadModules()
	}

	self.saveState = function() {
		var moduleStates = {}
		var moduleActive = {}

		modulesById.forEach(module => {
			if ("saveState" in module) {
				moduleStates[module.id] = module.saveState()
			}

			moduleActive[module.id] = module.isActive
		})

		return JSON.stringify({
			moduleStates: moduleStates,
			moduleActive: moduleActive
		})
	}

	self.loadState = function(stateStr) {
		try {
			var state = JSON.parse(stateStr)
			
			Object.getOwnPropertyNames(state.moduleActive).forEach(id => {
				if (modulesById.has(id)) {
					setModuleActive(modulesById.get(id), state.moduleActive[id])
				}
			})

			Object.getOwnPropertyNames(state.moduleStates).forEach(id => {
				if (modulesById.has(id)) {
					var module = modulesById.get(id)

					try {
						if ("loadState" in module) {
							module.loadState(state.moduleStates[id])
						}
					}
					catch (error) {
						// Error loading the previous state back in
						// Probable cause: The previous state is no longer compatible with the new code
						// => Clean the state

						if ("cleanState" in module) {
							module.cleanState()
						}

						console.error("Error loading previous state for " + module.id)
						console.error("Probable cause: The previous state is no longer compatible with the new code")
						console.error(error.toString())
					}
				}
			})
		}
		catch (error) {
			Console.error("Error loading previous state")
			Console.error(error.toString())
		}
	}

	return self
}


const Engine = _createEngine()

const saveState = () => Engine.saveState()
const loadState = stateStr => Engine.loadState(stateStr)

const onInit = () => Engine.onInit()

const onKeyDown = keyCode => Engine.onKeyDown(keyCode)
const onKeyUp = keyCode => Engine.onKeyUp(keyCode)

const onTick = () => Engine.onTick()
