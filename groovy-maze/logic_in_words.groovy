class Arg {
    String value
    Arg(value) {
        this.value = value
    }

    String toString() {
        value
    }
}

class Ghosts {
    def mode = new Arg("ghosts.mode")
}

class Rule {
    def event
    def object
    def operation
    Rule(event, object, operation) {
        this.event = event
        this.object = object
        this.operation = operation
    }

    String toString() {
        "on(${event}, ${operation.name}(${object}, ${operation.arg}))"
    }
}

class Operation {
    String name
    String arg
    Operation(name, arg) {
        this.name = name
        this.arg = arg
    }
}

class ObjectEvent {
    def object
    def trigger
    ObjectEvent(object) {
        this.object = object
    }
    String toString() {
        "${object}.${trigger}"
    }
}

class Trigger {
    def verb
    def receiver
    Trigger(verb, receiver) {
        this.verb = verb
        this.receiver = receiver
    }
    String toString() {
        "${verb}(${receiver})"
    }
}


class Logic {

    def rules = []
    def currentEvent = null
    def currentObject = null
    def currentOperation = null

    def changeEvent(newEvent) {
        if (!currentEvent.is(null)) {
            changeObject(null)
        }
        currentEvent = newEvent
        this
    }

    def changeObject(newObject) {
        assert !currentEvent.is(null)
        if (!currentObject.is(null)) {
            rules.add(new Rule(currentEvent, currentObject, currentOperation))
        }
        currentObject = newObject
        this
    }

    def changeOperation(op, arg) {
        currentOperation = new Operation(op, arg)
        this
    }

    def at(time) {
        changeEvent(time)
    }

    def when(object) {
        changeEvent(new ObjectEvent(object))
    }

    def and(object) {
        changeObject(object)
    }

    def then(object) {
        changeObject(object)
    }

    def getDies() {
        changeOperation("dies", null)
    }

    def becomes(arg) {
        changeOperation("setValue", arg)
    }

    def hits(arg) {
        currentEvent.trigger = new Trigger("hits", arg)
        this
    }

    def done() {
        changeObject(null)
    }
}
logic = new Logic()

ghost = new Arg("ghost")
ghosts = new Ghosts()
mode = new Arg("mode")
normal = new Arg("normal")
pill = new Arg("pill")
player = new Arg("player")
start = new Arg("start")
scared = new Arg("scared")
deleted = new Arg("deleted")
done = "done"

def at(time) {
    logic.at(time)
}

def when(object) {
    logic.when(object)
}

def rules(arg) {
    assert arg == "done"
    logic.done()
}

at start then ghosts.mode becomes normal
when ghost hits player then player dies
when player hits pill then ghosts.mode becomes scared and pill becomes deleted
rules done

logic.rules.each {println(it)}
