
function RotateRepeaterToNorth() {
    return {
        name: "RotateRepeaterToNorth",
        desc: "Rotates the repeater one block under the player",
        activeByDefault: true,
        onTick: () => {
            if (Input.isMouseDown(4)) {
                var pos = Player.getPos()
                pos[1] -= 1

                Chat.msg(JSON.stringify(World.getBlockState(pos)))

                // World.setBlock(pos, Blocks.UNPOWERED_REPEATER)
                World.setBlockState(pos, {facing: "north"})
                // World.setBlock(pos, Blocks.STONE)

                //pos[2] += 1
                //World.setBlock(pos, Blocks.REDSTONE_WIRE)
            }
        }
    }
}
Engine.registerModule( RotateRepeaterToNorth )


function CoroutineExample() {
    var id = Coroutine.createId();

    function* printNumbers() {
        for (var i = 1; i < 11; i++) {
            yield* Coroutine.waitForSeconds(0.5);
            
            Chat.msg(i);
        }

        Engine.deactivate(CoroutineExample)
    }

    return {
        name: "Coroutine Example",
        desc: "Prints the numbers 1 to 10",
        activeByDefault: true,

        onActivate() {
            Coroutine.start(printNumbers(), id)
        },

        onDeactivate() {
            Coroutine.stop(id);
        }
    }
}
Engine.registerModule( CoroutineExample )

function Parkour() {
    var lookahead = 2
    var lastPos = [0, 0, 0]

    return {
        name: "Parkour",
        activeByDefault: true,
        onTick: () => {
            if (! Player.isOnGround()) return
            KeyBind.jump.update()

            var pos = Player.getPos()
            pos[1] -= 1

            var vel = Player.getVel()
            //var vel = [(pos[0]-lastPos[0]), (pos[1]-lastPos[1]), (pos[2]-lastPos[2])]
            lastPos = pos
            var nextPos = [pos[0] + vel[0]*lookahead, pos[1], pos[2] + vel[2]*lookahead]

            if (World.getBlock(nextPos) == Blocks.AIR && World.getBlock(pos) != Blocks.AIR) {
                KeyBind.jump.setState(true);
            }
        }
    }
}

Engine.registerModule( Parkour )

function AutoClicker() {
    var lastClick = 0;

    return {
        name: "AutoClicker",
        desc: "Automatically clicks",
		activationKey: KEY_X,
		activeByDefault: true,
        onTick: () => {
            if (Input.isMouseDown(3)) {
                if (lastClick > 2) {
					Player.leftClick()
					lastClick = 0
                }
        
                lastClick ++
            }
        }
    }
}

Engine.registerModule( AutoClicker )


function AutoSprint() {
	return {
		name: "AutoSprint",
		desc: "Automatically sprints when you move forwards",
		activeByDefault: true,
		onTick: () => {
			if (Input.isKeyDown(KEY_W)) {
				KeyBind.sprint.setState(true);
			}
		}
	}
}

Engine.registerModule( AutoSprint )


function ToggleSneak() {
	return {
		name: "ToggleSneak",
		activationKey: KEY_C,
		onTick: () => {
			KeyBind.sneak.setState(true)
		},
		onDeactivate: () => {
			KeyBind.sneak.update()
		}
	}
}

Engine.registerModule( ToggleSneak )


function FullBright() {
    var normalGamma;

    return {
        name: "FullBright",
		activationKey: KEY_B,
		saveState: () => normalGamma,
		loadState: state => {normalGamma = state},
        onActivate: () => {
            normalGamma = Rendering.getGamma()
            Rendering.setGamma(100.0)
        },
        onDeactivate: () => {
            Rendering.setGamma(normalGamma)
        }
    }
}

Engine.registerModule( FullBright )


function Zoom() {
	var normalFOV;

	return {
		name: "Zoom",
		activationKey: KEY_Z,
		saveState: () => normalFOV,
		loadState: state => {normalFOV = state},
		onActivate: () => {
			normalFOV = Rendering.getFOV()
			Rendering.setFOV(20)
		},
		onDeactivate: () => {
			Rendering.setFOV(normalFOV)
		}
	}
}

Engine.registerModule( Zoom )
