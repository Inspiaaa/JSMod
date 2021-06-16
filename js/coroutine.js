
function _makeCoroutineManager() {
	var idToRunningCoroutine = new Map();

	function* _makeIdGenerator() {
		var idCounter = 0

		while (true) {
			yield idCounter++
		}
	}
	var _idGenerator = _makeIdGenerator();

	return {
		createId() {
			return _idGenerator.next().value;
		},

		start(coroutine, id=undefined) {
			if (typeof id === "undefined") {
				id = createId();
			}
	
			idToRunningCoroutine.set(id, coroutine);
			return id;
		},

		stop(id) {
			idToRunningCoroutine.delete(id);
		},

		onTick() {
			for (var entry of idToRunningCoroutine.entries) {
				var id, generator, result;

				[id, generator] = entry;

				try {
					result = generator.next();
				}
				catch (error) {
					console.error(error.toString())
					continue;
				}

				if (result.done) {
					idToRunningCoroutine.delete(id);
				}
			}
		}
	}
}


const Coroutine = _makeCoroutineManager();
