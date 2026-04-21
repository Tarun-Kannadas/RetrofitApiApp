const express = require('express')
const mongoose = require('mongoose')
const bcrypt = require('bcryptjs');

const app = express()
app.use(express.json())

mongoose.connect("mongodb://127.0.0.1:27017/notesDB")
    .then(() => console.log("Connected to MongoDB"))
    .catch(err => console.error("MongoDB connection error", err))

const UserSchema = new mongoose.Schema({
    name: String,
    email: {type: String, unique: true, required: true},
    password: {type: String, required: true}
})

const NoteSchema = new mongoose.Schema(
    {
        title: String,
        content: String,
        userId: {type: mongoose.Schema.Types.ObjectId , ref: 'User', required: true}
    },
    { timestamps: true  }
);

const User = mongoose.model('User', UserSchema)
const Note = mongoose.model('Note', NoteSchema)

app.get('/', async (req,res) => {
    res.send("Server is running...")
})

app.post("/register", async (req,res) => {
    try{
        const existingUser = await User.findOne({email: req.body.email})
        if(existingUser) return res.status(400).json({message: "Email already in use!"})

        const salt = await bcrypt.genSalt(10)
        const hashPassword = await bcrypt.hash(req.body.password, salt)

        const newUser = new User({
            name: req.body.name,
            email: req.body.email,
            password: hashPassword
        })

        await newUser.save()
        res.status(201).json({message:"User registered successfully!"})
    }
    catch(err) {
        res.status(500).json({error: err.message})
    }
})

app.post("/login", async (req,res) => {
    try {
        const user = await User.findOne({email: req.body.email})
        if(!user) return res.status(404).json({message:"User not found!"})

        const validPassword = await bcrypt.compare(req.body.password, user.password)
        if(!validPassword) return res.status(400).json({message:"Invalid Password"})

        res.status(200).json({
            message: "Logged in Successfully!", 
            userId: user._id 
        })
    } catch (err) {
        res.status(500).json({error: err.message})
    }
})

const requireUserId = (req, res, next) => {
    const userId = req.headers['user-id'];
    if (!userId) return res.status(401).json({ message: "User ID missing from headers" });
    
    req.userId = userId;
    next();
};

app.post('/notes', requireUserId, async (req, res) => {
    try {
        const newNote = new Note({
            title: req.body.title,
            content: req.body.content,
            userId: req.userId 
        });
        await newNote.save();
        res.status(201).json(newNote);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.get('/notes', requireUserId, async (req, res) => {
    try {
        const notes = await Note.find({ userId: req.userId });
        res.json(notes);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.put('/notes/:id', requireUserId, async (req, res) => {
    try {
        const updatedNote = await Note.findOneAndUpdate(
            { _id: req.params.id, userId: req.userId }, 
            req.body, 
            { new: true }
        );
        if (!updatedNote) return res.status(404).json({ message: "Note not found!" });
        res.json(updatedNote);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.delete('/notes/:id', requireUserId, async (req, res) => {
    try {
        const deletedNote = await Note.findOneAndDelete({ _id: req.params.id, userId: req.userId });
        if (!deletedNote) return res.status(404).json({ message: "Note not found!" });
        res.json({ message: "Note deleted Successfully!" });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.listen(3000, () => console.log("Server Running on port 3000"))